(defproject konvej "0.0.1"
  :description "Httpbin written in ring and Clojure."
  :url "https://github.com/maxcountryman/konvej"
  :license {:name "BSD"
            :url "http://opensource.org/licenses/BSD-3-Clause"}
  :dependencies [[clout "1.1.0"]
                 [cheshire "5.0.2"]
                 [org.clojure/clojure "1.4.0"]
                 [ring/ring-core "1.1.6"]
                 [ring/ring-jetty-adapter "1.1.6"]
                 [ring/ring-json "0.2.0"]]
  :min-lein-version "2.0.0"
  :plugins [[lein-ring "0.8.3"]]
  :ring {:handler konvej.verbs/app
         :adapter {:port 8080}})
