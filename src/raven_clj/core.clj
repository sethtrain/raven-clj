(ns raven-clj.core
  (:require [cheshire.core :as json]
            [clj-http.lite.client :as http]
            [clojure.java.io :as io]
            [clojure.string :as string])
  (:import [java.util Date UUID]
           [java.sql Timestamp]
           [java.net InetAddress]))

(defn version []
  (string/trim (slurp (io/resource "raven_clj/version.txt"))))

(def ^:private ^:const sentry-client
  "The name of this sentry client implementation"
  (format "raven-clj/%s" (version)))

(defn- generate-uuid []
  (string/replace (UUID/randomUUID) #"-" ""))

(defn make-sentry-url [uri project-id]
  (format "%s/api/%s/store/"
          uri project-id))

(defn make-sentry-header
  [ts key secret]
  (->> ["Sentry sentry_version=2.0"
        (format "sentry_client=%s" sentry-client)
        (format "sentry_timestamp=%s" ts)
        (format "sentry_key=%s" key)
        (when secret
          (format "sentry_secret=%s" secret))]
       (remove nil?)
       (string/join ", ")))

(defn send-packet [{:keys [ts uri project-id key secret] :as packet-info}]
  (let [url (make-sentry-url uri project-id)
        header (make-sentry-header ts key secret)
        body (dissoc packet-info :ts :uri :project-id :key :secret)]
    (http/post url
               {:throw-exceptions false
                :headers {"X-Sentry-Auth" header
                          "User-Agent" sentry-client}
                :body (json/generate-string body)})))

(defn parse-dsn [dsn]
  (let [[proto-auth url] (string/split dsn #"@")
        [protocol auth] (string/split proto-auth #"://")
        [key secret] (string/split auth #":")]
    {:key key
     :secret secret
     :uri (format "%s://%s" protocol
                  (string/join
                    "/" (butlast (string/split url #"/"))))
     :project-id (Integer/parseInt (last (string/split url #"/")))}))

(defn capture
  "Send a message to a Sentry server.
  event-info is a map that should contain a :message key and optional
  keys found at https://docs.getsentry.com/hosted/clientdev/#building-the-json-packet"
  [dsn event-info]
  (send-packet
   (merge (parse-dsn dsn)
          {:level "error"
           :platform "clojure"
           :server_name (.getHostName (InetAddress/getLocalHost))
           :ts (str (Timestamp. (.getTime (Date.))))}
          event-info
          {:event_id (generate-uuid)})))
