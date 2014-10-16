(ns raven-clj.ring
  (:require [raven-clj.core :refer [capture]]
            [raven-clj.interfaces :refer [http stacktrace]]))

(defn capture-error [dsn req extra ^Throwable error app-namespaces]
  (future (capture dsn (-> (merge extra
                                  {:message (.getMessage error)})
                           (http req)
                           (stacktrace error app-namespaces)))))

(defn wrap-sentry [handler dsn & [extra app-namespaces]]
  (fn [req]
    (try
      (handler req)
      (catch Exception e
        (capture-error dsn req extra e app-namespaces)
        (throw e))
      (catch AssertionError e
        (capture-error dsn req extra e app-namespaces)
        (throw e)))))
