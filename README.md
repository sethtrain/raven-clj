# raven-clj

A Clojure interface to Sentry.

## Usage

```clojure
[raven-clj "1.2.0"]
```

### `capture`

The `capture` function is a general use function that could be placed throughout your Clojure code to log information to your Sentry server.

```clojure
(def dsn "https://b70a31b3510c4cf793964a185cfe1fd0:b7d80b520139450f903720eb7991bf3d@example.com/1")

(capture dsn {:message "Test Exception Message"
             :tags {:version "1.0"}
             :logger "main-logger"
             :extra {:my-key 1
                     :some-other-value "foo bar"}})

;; Associates:
;; "sentry.interfaces.Http"
;;  {:url "http://localhost:3000/"
;;   :scheme "http"
;;   :server_name "locahost:3000"
;;   :event_id "<generated UUID>"
;;   :uri "/"
;;   :method "POST"
;;   :data {:item "1"}}}
;; with event-info map
(capture dsn
        (-> {:message "Test HTTP Exception"
             :tags {:testing "1.0"}}
            (interfaces/http request)))

;; Associates:
;; "sentry.interfaces.Stacktrace"
;;  {:frames [{:filename "..." :function "..." :lineno 1}...]}
;; with event-info map
;; Optionally pass your app's namespaces as the final arg to stacktrace
(capture dsn
        (-> {:message "Test Stacktrace Exception"}
            (interfaces/stacktrace (Exception.) ["myapp.ns"])))
```

#### Note about event-info map

In the `capture` function I use merge to merge together the final packet to send to Sentry.  The only fields that can't be overwritten when sending information
to `capture` is `event-id` and `timestamp`.  Everything else can be overwritten by passing along a new value for the key:

```clojure
(capture dsn
        (-> {:message "Test Stacktrace Exception"
             :logger "application-logger"}
            (interfaces/stacktrace (Exception.))))
```

Please refer to [Building the JSON Packet](http://sentry.readthedocs.org/en/latest/developer/client/index.html#building-the-json-packet) for more information on what
attributes are allowed within the packet sent to Sentry.

## Ring middleware

raven-clj also includes a Ring middleware that sends the Http and Stacktrace interfaces for Sentry packets.  Usage (for Compojure):

```clojure
(use 'raven-clj.ring)

(def dsn "https://b70a31b3510c4cf793964a185cfe1fd0:b7d80b520139450f903720eb7991bf3d@example.com/1")

;; If you want to fully utilize the Http interface you should make sure
;; you use the wrap-params and wrap-keyword-params middlewares to ensure
;; the request data is stored correctly.
(-> routes
    (wrap-sentry dsn)
    (handler/site))

;; You could also include some of the optional attributes and pass
;; your app's namespace prefixes so your app's stack frames are
;; highlighted in sentry
(-> routes
    (wrap-sentry dsn {:tags {:version "1.0"}} ["myapp" "com.mylib"])
    (handler/site))
```

## License

Copyright © 2013, 2014 Seth Buntin

Distributed under the Eclipse Public License, the same as Clojure.
