# raven-clj

A Clojure interface to Sentry.

## Usage

```clojure
[raven-clj "0.1.0"]
```

### `notify`

The `notify` function is a general use function that could be placed throughout your Clojure code to log information to your Sentry server.

```clojure
(def config {:domain "http://localhost:9000"
             :project-id 2
             :key "ef2f603f4c284b1287c65df7debc966c"
             :secret "0bb4200b556d4fb59b0606d2e75f41a2"
             ;; Optional
             :logger "raven-clj"})

;; Adds:
;; "sentry.interfaces.Http"
;;  {:url "http://localhost:3000/"
;;   :scheme "http"
;;   :server-name "locahost:3000"
;;   :uri "/"
;;   :method "POST"
;;   :data {:item "1"}}}
;; to event-info map
(notify config
        (-> {:message "Test HTTP Exception"
             :tags {:testing "1.0"}}
            (interfaces/http request)))

;; Add
;; "sentry.interfaces.Stacktrace"
;;  {:frames [{:filename "..." :function "..." :lineno 1}...]}
;; to event-info map
(notify config
        (-> {:message "Test Stacktrace Exception"}
            (interfaces/stacktrace (Exception.))))
```

## Ring middleware

raven-clj also includes a Ring middleware that sends the Http and Stacktrace interfaces for Sentry packets.  Usage (for Compojure):

```clojure
(def config {:domain "http://localhost:9000"
             :project-id 2
             :key "ef2f603f4c284b1287c65df7debc966c"
             :secret "0bb4200b556d4fb59b0606d2e75f41a2"
             ;; Optional
             :logger "raven-clj"})

;; If you want to fully utilize the Http interface you should make sure
;; you use the wrap-params and wrap-keyword-params middlewares to ensure
;; the request data is stored correctly.
(-> (handler/site routes)
    (wrap-sentry config)
    (wrap-params)
    (wrap-keyword-params))
```

## License

Copyright © 2013 Seth Buntin

Distributed under the Eclipse Public License, the same as Clojure.
