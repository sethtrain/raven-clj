(ns raven-clj.core
  (:require [cheshire.core :as json]
            [clj-http.client :as http]
            [clojure.string :as string])
  (:import [java.util Date UUID]
           [java.sql Timestamp]
           [java.net InetAddress]))

(defn generate-uuid []
  (string/replace (UUID/randomUUID) #"-" ""))

(defn make-sentry-url [domain project-id]
  (format "%s/api/%s/store/"
          domain project-id))

(defn make-sentry-header [ts key secret]
  (format "Sentry sentry_version=2.0, sentry_client=raven-clj/1.0, sentry_timestamp=%s, sentry_key=%s, sentry_secret=%s"
          ts key secret))

(defn send-packet [{:keys [ts domain project-id key secret] :as packet-info}]
  (let [url (make-sentry-url domain project-id)
        header (make-sentry-header ts key secret)]
    (http/post url
               {:throw-exceptions false
                :headers {"X-Sentry-Auth" header
                          "User-Agent" "raven-clj/1.0"}
                :body (json/generate-string packet-info)})))

(defn notify [config event-info]
  "Send a message to a Sentry server.
  event-info is a map that should contain a :message key and optional
  keys found at http://sentry.readthedocs.org/en/latest/developer/client/index.html#building-the-json-packet"
  (send-packet
    (merge config
           {:level "error"
            :plaform "clojure"
            :ts (str (Timestamp. (.getTime (Date.))))}
           event-info
           {:event-id (generate-uuid)
            :server-name (.getHostName (InetAddress/getLocalHost))})))
