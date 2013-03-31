(ns verbage.verbs
  (:require [clout.core :refer :all]
            [cheshire.core :refer [generate-string]]
            [ring.middleware.session :refer :all]
            [ring.middleware.params :refer :all]
            [ring.util.response :refer :all]
            [ring.adapter.jetty :refer :all]))


(defn method-not-allowed
  [allowed & body]
  {:status 405
   :headers {"Allow" allowed}
   :body (or body "")})


(defn json-response
  [body]
  (->
    (response (generate-string body {:pretty true}))
    (content-type "application/json")))


(defn index
  [req]
  (if (= (:uri req) "/")
    (response "try: /get, /ip, /headers /user-agent")
    req))


(defn ip
  [req]
  (if (= (:uri req) "/ip")
    (json-response {:origin (:remote-addr req)})
    req))


(defn headers
  [req]
  (if (= (:uri req) "/headers")
    (->
      (json-response {:headers (:headers req)}))
    req))


(defn user-agent
  [req]
  (if (= (:uri req) "/user-agent")
    (->
      (json-response {:headers (get-in req [:headers "user-agent"])}))
    req))


(defn http-get
  [req]
  (if (= (:uri req) "/get")
    (if (not (some #(= (:request-method req) %) [:head :options :get]))
      (method-not-allowed "HEAD, OPTIONS, GET")
      (let [scheme (name (:scheme req))
            host (get-in req [:headers "host"])
            uri (:uri req)
            query-string (if (:query-string req)
                          (str "?" (:query-string req))
                          (:query-string req))
            url (str scheme "://" host uri query-string)
            resp {:url url
                  :headers (:headers req)
                  :args (or (:query-params req) {})
                  :origin (:remote-addr req)}]
            (->
              (response (generate-string resp {:pretty true}))
              (content-type "application/json"))))
      req))


(defn foo
  [req]
  (if (:uri req)
    (if-let [params (route-matches "/foo/:bar" req)]
      (response (:bar params)))
    req))


(def four-oh-four
  (->
    (response "<h1>Page Not Found</h1>")
    (content-type "text/html")))


(defn routes
  [req]
  (prn (:remote-addr req))
  (let [resp (->
               req
               index
               ip
               headers
               user-agent
               http-get
               foo)]
    (if (:body resp)
      resp
      four-oh-four)))


(def app
  (->
    routes
    wrap-params
    wrap-session))


(defn -main [port]
  (run-jetty app {:port (Integer. port)}))
