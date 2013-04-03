(ns verbage.runner
  (:require [ring.adapter.jetty :refer [run-jetty]]
            [verbage.verbs :refer [app]]))

(defn -main [port]
  (run-jetty app {:port (Integer. port)}))
