(set-env!
 :resource-paths #{"src" "resources"}
 :dependencies '[[org.clojure/clojure "1.8.0" :scope "provided"]
                 [cheshire "5.0.1"]
                 [clj-http "3.0.1"]
                 [prone "1.0.1"]

                 [adzerk/bootlaces "0.1.13" :scope "test"]
                 [adzerk/boot-test "1.1.1"  :scope "test"]])

(require '[raven-clj.core :as raven-clj]
         '[adzerk.boot-test :refer [test]]
         '[adzerk.bootlaces :refer [bootlaces! build-jar push-release]])

(def +version+ (raven-clj/version))

(bootlaces! +version+ :dont-modify-paths? true)

(task-options!
 pom {:project 'raven-clj
      :version +version+
      :description "Sentry clojure client"
      :url "http://github.com/sethtrain/raven-clj"
      :scm {:url "http://github.com/sethtrain/raven-clj"}
      :license {"EPL" "http://www.eclipse.org/legal/epl-v10.html"}})

(deftask test! []
  (merge-env! :source-paths #{"test"})
  (test))
