(ns raven-clj.ring
  (:require [raven-clj.core :refer [notify]]
            [raven-clj.interfaces :refer [http stacktrace]]))

(defn wrap-sentry [handler config]
  (fn [req]
    (try
      (handler req)
      (catch Exception e
        (notify config (-> {:message (.getMessage e)}
                           (http req)
                           (stacktrace e)))
        (throw e)))))
