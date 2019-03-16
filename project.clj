(defproject raven-clj "0.0.0-dont-deploy"
  :description "Sentry clojure client"
  :url "http://github.com/sethtrain/raven-clj"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1" :scope "provided"]
                 [cheshire "5.8.0"]
                 [clj-http-lite "0.3.0"]
                 [prone "1.0.1"]
                 [ring/ring-codec "1.1.1"]])
