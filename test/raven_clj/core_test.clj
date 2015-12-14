(ns raven-clj.core-test
  (:use clojure.test
        raven-clj.core)
  (:import [java.sql Timestamp]
           [java.util Date]))

(println "Testing Clojure version" (clojure-version))

(def example-dsn
  (str "https://"
       "b70a31b3510c4cf793964a185cfe1fd0:b7d80b520139450f903720eb7991bf3d"
       "@example.com/1"))

(deftest test-make-sentry-url
  (testing "secure url"
    (is (= (make-sentry-url "https://example.com" "1")
           "https://example.com/api/1/store/")))
  (testing "insecure url"
    (is (= (make-sentry-url "http://example.com" "1")
           "http://example.com/api/1/store/"))))

(deftest test-make-sentry-header
  (testing "sentry header"
    (let [ts (str (Timestamp. (.getTime (Date.))))]
      (is (= (make-sentry-header ts
                                 "b70a31b3510c4cf793964a185cfe1fd0"
                                 "b7d80b520139450f903720eb7991bf3d")
             (format "Sentry sentry_version=2.0, sentry_client=raven-clj/0.6.0, sentry_timestamp=%s, sentry_key=b70a31b3510c4cf793964a185cfe1fd0, sentry_secret=b7d80b520139450f903720eb7991bf3d" ts))))))

(deftest test-parse-dsn
  (testing "dsn parsing"
    (is (= (parse-dsn "https://b70a31b3510c4cf793964a185cfe1fd0:b7d80b520139450f903720eb7991bf3d@example.com/1")
           {:key "b70a31b3510c4cf793964a185cfe1fd0"
            :secret "b7d80b520139450f903720eb7991bf3d"
            :uri "https://example.com"
            :project-id 1})))

  (testing "dsn parsing with path"
    (is (= (parse-dsn "https://b70a31b3510c4cf793964a185cfe1fd0:b7d80b520139450f903720eb7991bf3d@example.com/sentry/1")
           {:key "b70a31b3510c4cf793964a185cfe1fd0"
            :secret "b7d80b520139450f903720eb7991bf3d"
            :uri "https://example.com/sentry"
            :project-id 1})))

  (testing "dsn parsing with port"
    (is (= (parse-dsn "https://b70a31b3510c4cf793964a185cfe1fd0:b7d80b520139450f903720eb7991bf3d@example.com:9000/1")
           {:key "b70a31b3510c4cf793964a185cfe1fd0"
            :secret "b7d80b520139450f903720eb7991bf3d"
            :uri "https://example.com:9000"
            :project-id 1})))

  (testing "dsn parsing with port and path"
    (is (= (parse-dsn "https://b70a31b3510c4cf793964a185cfe1fd0:b7d80b520139450f903720eb7991bf3d@example.com:9000/sentry/1")
           {:key "b70a31b3510c4cf793964a185cfe1fd0"
            :secret "b7d80b520139450f903720eb7991bf3d"
            :uri "https://example.com:9000/sentry"
            :project-id 1}))))

(deftest test-capture
  (testing "capture"
    (testing "with a valid dsn"
      (let [event-info (atom nil)]
        (with-redefs [send-packet (fn [ev] (reset! event-info ev))]
          (capture example-dsn {})
          (is (= (:platform @event-info) "clojure")
              "should set :platform in event-info to clojure"))))))
