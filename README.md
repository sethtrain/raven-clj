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

(notify config
        {:message "Test HTTP Exception"
         "sentry.interfaces.Http"
         {:url "http://localhost:3000/"
          :scheme "http"
          :server-name "locahost:3000"
          :uri "/"
          :method "POST"
          :data {:item "1"}}})

(notify config
        {:message "Test HTTP Exception"
         :tags {:testing "1.0"}
         "sentry.interfaces.Http"
         {:url "http://localhost:3000/"
          :scheme "http"
          :server-name "locahost:3000"
          :uri "/"
          :method "POST"
          :data {:item "1"}}})

(notify config
        {:message "Test Stacktrace Exception"
         "sentry.interfaces.Stacktrace"
         (stacktrace->sentry-interface (.getStackTrace (Exception.)))})
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

(-> (handler/site routes)
    (wrap-sentry config))
```

## License

Copyright © 2013 Seth Buntin

Distributed under the Eclipse Public License, the same as Clojure.
