(ns konvej.verbs
  (:require [cheshire.core :refer [generate-string]]
            [clout.core :refer [route-matches]]
            [clojure.string :refer [join
                                    upper-case]]
            [konvej.util :refer [defroute
                                 html-response
                                 json-response
                                 method-not-allowed
                                 not-found
                                 permanent-redirect
                                 wrap-routes]]
            [ring.middleware.head :refer [wrap-head]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.session :refer [wrap-session]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.util.response :refer [content-type
                                        redirect
                                        response]]))


(defn- get-url
  [req]
  (str (name (:scheme req))
       "://"
       (get-in req [:headers "host"])
       (:uri req)
       (if (:query-string req)
         (str "?" (:query-string req))
         (:query-string req))))


(defn- get-resp-map
  [req]
  (let [m {:origin (or (get-in req [:headers "x-forwarded-for"])
                       (:remote-addr req))
           :url (get-url req)
           :args (:query-params req)
           :headers (:headers req)}]
    (if-let [form-params (:form-params req)]
      (assoc m :form form-params)
      m)))


(defroute index "/" [:any]
  (fn [req]
    (response (str "endpoints: /get, /post, /put/, delete, /ip, /headers "
                   "/user-agent /redirect/:n"))))


(defroute http-get "/get" [:get :head :options]
  (fn [req] (json-response (get-resp-map req))))


(defroute http-post "/post" [:post]
  (fn [req] (json-response (get-resp-map req))))


(defroute http-put "/put" [:put]
  (fn [req] (json-response (get-resp-map req))))


(defroute http-delete "/delete" [:delete]
  (fn [req] (json-response (get-resp-map req))))


(defroute robots "/robots.txt" [:any]
  (fn [req] (->
              (response "User-agent: *\nDisallow: /deny")
              (content-type "text/plain"))))


(defroute ip "/ip" [:any]
  (fn [req] (json-response {:origin (:origin (get-resp-map req))})))


(defroute headers "/headers" [:any]
  (fn [req] (json-response {:headers (:headers (get-resp-map req))})))


(defroute user-agent "/user-agent" [:any]
  (fn [req]
    (json-response {:headers (get-in (get-resp-map req)
                                     [:headers "user-agent"])})))


(defroute redirects "/redirect/:n" [:get]
  (fn [req]
    (let [params (:route-params req)
          n (dec (Integer. (:n params)))]
      (assert (> n 0))
      (if (= n 1)
        (permanent-redirect "/get")
        (permanent-redirect (str "/redirect/" n))))))


(defroute deny "/deny" [:any]
  (fn [req]
    (response "")))


(defroute foo "/foo/:bar" [:get]
  (fn [req] (->
              req
              :route-params
              :bar
              response)))


(def app
  (->
    (partial wrap-routes 'konvej.verbs)
    (wrap-head)
    (wrap-params)
    (wrap-session)))
