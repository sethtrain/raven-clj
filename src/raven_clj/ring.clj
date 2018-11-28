(ns raven-clj.ring
  (:require [raven-clj.core :refer [capture]]
            [raven-clj.interfaces :refer [http stacktrace]]))

;; Limit the size of the extra contextual data
;; https://docs.sentry.io/clientdev/data-handling/
(defn- truncate-extra-str [text]
  (subs text 0 (min (count text) 4096)))

(defn capture-error [dsn req ^Throwable error extra app-namespaces http-alter-fn]
  (future (capture dsn (-> (merge extra
                                  {:message (.getMessage error)
                                   :extra {:ex-data (truncate-extra-str (str (ex-data error)))}})
                           (http req http-alter-fn)
                           (stacktrace error app-namespaces)))))

(defn wrap-sentry [handler dsn & [opts]]
  (fn [req]
    (let [alter-fn (or (:http-alter-fn opts)
                       identity)]
      (try
        (handler req)
        (catch Throwable e
          (capture-error dsn req e (:extra opts) (:namespaces opts) alter-fn)
          (throw e))))))
