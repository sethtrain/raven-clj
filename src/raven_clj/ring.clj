(ns raven-clj.ring
  (:require [raven-clj.core :refer [capture]]
            [raven-clj.interfaces :refer [http stacktrace]]))

(defn capture-error [dsn req ^Throwable error extra app-namespaces http-alter-fn]
  (future (capture dsn (-> (merge extra
                                  {:message (.getMessage error)})
                           (http req http-alter-fn)
                           (stacktrace error app-namespaces)))))

(defn wrap-sentry [handler dsn & [opts] [extra app-namespaces]]
  (fn [req]
    (let [alter-fn (or (:http-alter-fn opts)
                       identity)]
      (try
        (handler req)
        (catch Exception e
          (capture-error dsn req e (:extra opts) (:namespaces opts) alter-fn)
          (throw e))
        (catch AssertionError e
          (capture-error dsn req e (:extra opts) (:namespaces opts) alter-fn)
          (throw e))))))
