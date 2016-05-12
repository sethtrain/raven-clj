(ns raven-clj.interfaces
  (:require [prone.stacks :as stacks]))

(defn- make-http-info [req]
  {:url (str (name (:scheme req))
             "://"
             (:server-name req)
             (if (not= 80 (:server-port req))
               (str ":" (:server-port req)))
             (:uri req))
   :method (:method req)
   :headers (get req :headers {})
   :query_string (get req :query-string "")
   :data (get req :params {})
   :env {:session (get req :session {})}})

(defn http [event-map req alter-fn]
  (assoc event-map "sentry.interfaces.Http"
         (alter-fn (make-http-info req))))

(defn file->source [file-path line-number]
  (some-> (io/resource file-path)
    slurp
    (str/split #"\n")
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
     :context_line (nth source 5)
     :pre_context  (take 5 source)
     :post_context (drop 6 source)}))

(defn stacktrace [event-map ^Exception e & [app-namespaces]]
  (let [stacks (stacks/normalize-exception (stack/root-cause e))
        frames  (map (partial frame->sentry app-namespaces)
                  (reverse (:frames stacks)))]
    (assoc event-map
      :exception [{:value      (:message stacks)
                   :type       (:type stacks)
                   :stacktrace {:frames frames}}])))
