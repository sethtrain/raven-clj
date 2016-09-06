(set-env!
 :resource-paths #{"src"}
 :dependencies '[[org.clojure/clojure "1.8.0" :scope "provided"]
                 [cheshire "5.0.1"]
                 [clj-http "3.0.1"]
                 [prone "1.0.1"]

                 [adzerk/boot-test "1.1.1" :scope "test"]])

(task-options!
 pom {:project 'raven-clj
      :version "1.4.3"
      :description "Sentry clojure client"
      :url "http://github.com/sethtrain/raven-clj"
      :license {"EPL" "http://www.eclipse.org/legal/epl-v10.html"}})

(require '[adzerk.boot-test :refer [test]])

(deftask build []
  (comp (pom) (jar)))

(deftask test! []
  (merge-env! :source-paths #{"test"})
  (test))
