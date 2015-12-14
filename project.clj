(defproject raven-clj "1.3.1"
  :description "Sentry clojure client"
  :url "http://github.com/sethtrain/raven-clj"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[cheshire "5.0.1"]
                 [clj-http "2.0.0"]]
  :profiles {:dev {:dependencies [[org.clojure/clojure "1.7.0"]]}
             :clj-1.5 {:dependencies [[org.clojure/clojure "1.5.1"]]}
             :clj-1.6 {:dependencies [[org.clojure/clojure "1.6.0"]]}
             :clj-1.7 {:dependencies [[org.clojure/clojure "1.7.0"]]}
             :clj-1.8 {:dependencies [[org.clojure/clojure "1.8.0-RC3"]]}})
