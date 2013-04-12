(ns konvej.util
  (:require [cheshire.core :refer [generate-string]]
            [ring.util.response :refer [content-type
                                        response]]))


(defn json-response
  [body]
  (->
    (response (generate-string body {:pretty true}))
    (content-type "application/json")))


(defn permanent-redirect
  [url]
  {:status 301
   :headers {"Location" url}
   :body ""})
