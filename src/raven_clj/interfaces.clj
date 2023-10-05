(ns raven-clj.interfaces
  (:require
    [clojure.java.io :as io]
    [clojure.string :as string]
    [prone.stacks :as prone-stack]))

(defn- make-http-info [req]
  {:url (str (name (:scheme req))
             "://"
             (:server-name req)
             (if (not= 80 (:server-port req))
               (str ":" (:server-port req)))
             (:uri req))
   :method (:request-method req)
   :headers (get req :headers {})
   :query_string (get req :query-string "")
   :data (get req :params {})
   :env {:session (get req :session {})}})

(defn http [event-map req alter-fn]
  (assoc event-map "sentry.interfaces.Http"
         (alter-fn (make-http-info req))))

(defn file->source [file-path line-number]
  (some-> file-path
    (io/resource)
    slurp
    (string/split #"\n")
    (#(drop (- line-number 6) %))
    (#(take 11 %))))

(defn in-app [package app-namespaces]
  (boolean (some #(.startsWith package %) app-namespaces)))

(defn frame->sentry [app-namespaces frame]
  (let [source (file->source (:class-path-url frame) (:line-number frame))]
    {:filename     (:file-name frame)
     :lineno       (:line-number frame)
     :function     (str (:package frame) "/" (:method-name frame))
     :in_app       (in-app (:package frame) app-namespaces)
     :context_line (nth source 5 nil)
     :pre_context  (take 5 source)
     :post_context (drop 6 source)}))

(defn- exception-seq [ex]
  (lazy-seq (cons ex (when-let [cause (:caused-by ex)]
                       (exception-seq cause)))))

(defn- flatten-data [data]
  (into {} (map (fn [[k v]]
                  [(pr-str k) (pr-str v)])
                data)))

(defn- exception->sentry [app-namespaces {:keys [data frames message type]}]
  {:value      message
   :type       type
   ; "The list is ordered from caller to callee, or oldest to youngest.
   ; The last frame is the one creating the exception."
   ; https://develop.sentry.dev/sdk/event-payloads/stacktrace/
   :stacktrace {:frames (map (partial frame->sentry app-namespaces)
                             (reverse frames))}
   :mechanism  (cond-> {:type "generic"}
                       data (assoc :data (flatten-data data)))})

(defn stacktrace [event-map ^Exception e & [app-namespaces]]
  (assoc event-map
    ; "Multiple values represent chained exceptions and should be sorted oldest to newest."
    ; https://develop.sentry.dev/sdk/event-payloads/exception/
    :exception (map (partial exception->sentry app-namespaces)
                    (reverse (exception-seq (prone-stack/normalize-exception e))))))
