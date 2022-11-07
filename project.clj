(defn- get-version
  ;; See README for notes on why.
  "Grab the version for the project."
  []
  (require '[clojure.string :as str]
           '[clojure.java.io :as io])
  ;; since it is so early in the boot process, it
  ;; has to be io/file, and not io/resource
  (str/trim (slurp (io/file "resources/raven_clj/version.txt"))))

(defproject raven-clj (get-version)
  :description "Sentry clojure client"
  :url "http://github.com/sethtrain/raven-clj"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.11.1" :scope "provided"]
                 [cheshire "5.11.0"]
                 [org.clj-commons/clj-http-lite "1.0.13"]
                 [prone "2021-04-23"]])
