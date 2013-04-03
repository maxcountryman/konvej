(ns konvej.runner
  (:require [ring.adapter.jetty :refer [run-jetty]]
            [konvej.verbs :refer [app]]))

(defn -main [port]
  (run-jetty app {:port (Integer. port)}))
