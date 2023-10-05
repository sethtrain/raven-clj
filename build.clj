(ns build
  (:require
    [clojure.java.io :as io]
    [clojure.string :as str]
    [clojure.tools.build.api :as b]))

(def lib 'raven-clj/raven-clj)
(def version (str/trim (slurp (io/file "resources/raven_clj/version.txt"))))
(def class-dir "target/classes")
(def basis (b/create-basis {:project "deps.edn"}))
(def jar-file (format "target/%s-%s.jar" (name lib) version))

(defn clean [_]
  (b/delete {:path "target"}))

(defn jar [_]
  (b/write-pom {:class-dir class-dir
                :lib       lib
                :version   version
                :basis     basis
                :src-dirs  ["src"]})
  (b/copy-dir {:src-dirs   ["src" "resources"]
               :target-dir class-dir})
  (b/jar {:class-dir class-dir
          :jar-file  jar-file}))
