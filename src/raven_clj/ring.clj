(ns raven-clj.ring
  (:require [raven-clj.core :refer [capture]]
            [raven-clj.interfaces :refer [http stacktrace]]))

(defn capture-error [dsn req extra ^Throwable error]
  (future (capture dsn (-> (merge extra
                                  {:message (.getMessage error)})
                           (http req)
                           (stacktrace error)))))

(defn wrap-sentry [handler dsn & [extra]]
  (fn [req]
    (try
      (handler req)
      (catch Exception e
        (capture-error dsn req extra e)
        (throw e))
      (catch AssertionError e
        (capture-error dsn req extra e)
        (throw e)))))
