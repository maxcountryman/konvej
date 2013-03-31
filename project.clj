(defproject verbage "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[clout "1.1.0"]
                 [cheshire "5.0.2"]
                 [org.clojure/clojure "1.4.0"]
                 [ring/ring-core "1.1.6"]
                 [ring/ring-jetty-adapter "1.1.6"]
                 [ring/ring-json "0.2.0"]]
  :min-lein-version "2.0.0"
  :plugins [[lein-ring "0.8.3"]]
  :ring {:handler verbage.verbs/app
         :adapter {:port 8080}})
