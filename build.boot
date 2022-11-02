(set-env!
 :resource-paths #{"src" "resources"}
 :dependencies '[[org.clojure/clojure "1.11.1" :scope "provided"]
                 [cheshire "5.11.0"]
                 [org.clj-commons/clj-http-lite "1.0.13"]
                 [prone "2021-04-23"]

                 [adzerk/bootlaces "0.2.0" :scope "test"]
                 [adzerk/boot-test "1.2.0"  :scope "test"]])

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
