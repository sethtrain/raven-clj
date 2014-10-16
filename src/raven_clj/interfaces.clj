(ns raven-clj.interfaces)

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

(defn http [event-map req]
  (assoc event-map "sentry.interfaces.Http"
         (make-http-info req)))

(defn- make-frame [^StackTraceElement element app-namespaces]
  {:filename (.getFileName element)
   :lineno (.getLineNumber element)
   :function (str (.getClassName element) "." (.getMethodName element))
   :in_app (boolean (some #(.startsWith (.getClassName element) %) app-namespaces))})

(defn- make-stacktrace-info [elements app-namespaces]
  {:frames (reverse (map #(make-frame % app-namespaces) elements))})

(defn stacktrace [event-map ^Exception e & [app-namespaces]]
  (assoc event-map
    :exception [{:stacktrace (make-stacktrace-info (.getStackTrace e) app-namespaces)
                 :type (str (class e))
                 :value (.getMessage e)}]))
