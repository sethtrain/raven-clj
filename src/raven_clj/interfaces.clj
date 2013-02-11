(ns raven-clj.interfaces)

(defn make-http-info [req]
  {:url (str (name (:scheme req))
             "://"
             (:server-name req)
             (if (not= 80 (:server-port req))
               (str ":" (:server-port req)))
             (:uri req))
   :method (:method req)
   :headers (get req :headers {})
   :query_string (get req :query-string "")
   :data (get req :params {})})

(defn http [event-map req]
  (assoc event-map "sentry.interfaces.Http"
         (make-http-info req)))

(defn make-frame [element]
  {:filename (.getFileName element)
   :lineno (.getLineNumber element)
   :function (.getMethodName element)})

(defn make-stacktrace-info [elements]
  {:frames (map make-frame elements)})

(defn stacktrace [event-map e]
  (assoc event-map "sentry.interfaces.Stacktrace"
         (make-stacktrace-info (.getStackTrace e))))
