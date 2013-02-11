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

;; Associates:
;; "sentry.interfaces.Http"
;;  {:url "http://localhost:3000/"
;;   :scheme "http"
;;   :server-name "locahost:3000"
;;   :uri "/"
;;   :method "POST"
;;   :data {:item "1"}}}
;; with event-info map
(notify config
        (-> {:message "Test HTTP Exception"
             :tags {:testing "1.0"}}
            (interfaces/http request)))

;; Associates:
;; "sentry.interfaces.Stacktrace"
;;  {:frames [{:filename "..." :function "..." :lineno 1}...]}
;; with event-info map
(notify config
        (-> {:message "Test Stacktrace Exception"}
            (interfaces/stacktrace (Exception.))))
```

#### Note about event-info map

In the `notify` function I use merge to merge together the final packet to send to Sentry.  The only fields that can't be overwritten when sending information
to `notify` is `event-id` and `timestamp`.  Everything else can be overwritten by passing along the new value for the key.  For instance, I set the platform for
all Sentry log items to "clojure" to override this just pass the new value, from the example above, in the map with they key of `message`.  So you will then have:

```clojure
notify config
        (-> {:message "Test Stacktrace Exception"
             :platform "clj"}
            (interfaces/stacktrace (Exception.))))
```

Plese refer to [Building the JSON Packet](http://sentry.readthedocs.org/en/latest/developer/client/index.html#building-the-json-packet) for more information on what
attributes are allowed within the packet sent to Sentry.

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
(-> routes
    (wrap-sentry config)
    (handler/site))
```

## License

Copyright © 2013 Seth Buntin

Distributed under the Eclipse Public License, the same as Clojure.
