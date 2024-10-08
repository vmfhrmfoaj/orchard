(ns orchard.java.parser-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [orchard.misc :as misc])
  (:import
   (orchard.java DummyClass)))

(def source-info
  (when (>= misc/java-api-version 9)
    (misc/require-and-resolve 'orchard.java.parser/source-info)))

(when source-info
  (deftest source-info-test
    (is (class? DummyClass))

    (testing "file on the filesystem"
      (is (= {:class 'orchard.java.DummyClass,
              :members
              '{dummyMethod
                {[]
                 {:name dummyMethod,
                  :type java.lang.String,
                  :argtypes [],
                  :argnames [],
                  :non-generic-argtypes []
                  :doc "Method-level docstring. @return the string \"hello\"",
                  :line 18,
                  :column 5}}
                orchard.java.DummyClass
                {[]
                 {:name orchard.java.DummyClass,
                  :type void,
                  :argtypes [],
                  :non-generic-argtypes [],
                  :argnames [],
                  :doc nil,
                  :line 12,
                  :column 8}}},
              :doc
              "Class level docstring.\n\n```\n   DummyClass dc = new DummyClass();\n```\n\n@author Arne Brasseur",
              :line 12,
              :column 1,
              :file "orchard/java/DummyClass.java"
              :resource-url (.toURL (java.net.URI. (str "file:"
                                                        (System/getProperty "user.dir")
                                                        "/test-java/orchard/java/DummyClass.java")))}
             (dissoc (source-info 'orchard.java.DummyClass)
                     :path))))

    (testing "java file in a jar"
      (let [rt-info (source-info 'clojure.lang.RT)]
        (is (= {:file "clojure/lang/RT.java"}
               (select-keys rt-info [:file])))
        (is (re-find #"jar:file:/.*/.m2/repository/org/clojure/clojure/.*/clojure-.*-sources.jar!/clojure/lang/RT.java"
                     (str (:resource-url rt-info))))))))
