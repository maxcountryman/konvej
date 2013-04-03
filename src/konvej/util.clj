(ns konvej.util
  (:require [cheshire.core :refer [generate-string]]
            [clout.core :refer [route-matches]]
            [clojure.string :refer [join
                                    upper-case]]
            [ring.middleware.head :refer [wrap-head]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.session :refer [wrap-session]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.util.response :refer [content-type
                                        redirect
                                        response]]))


(defn- render-method-not-allowed
  [meth]
  (str
    "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 3.2 Final//EN\">
    <title>405 Method Not Allowed</title>
    <h1>Method Not Allowed</h1>
    <p>The method " meth " is not allowed for the requested URL.</p>"))


(defn method-not-allowed
  [meth allowed]
  {:status 405
   :headers {"Allow" (upper-case (name allowed))}
   :body (render-method-not-allowed (upper-case (name meth)))})


(defn get-allowed
  [meths]
  (join " " (map #(upper-case (name %)) meths)))


(defmacro defroute
  [routename uri meths handler]
  `(defn
     ~(with-meta routename (assoc (meta routename) :route-handler true))
    [req#]
    (let [req-meth# (:request-method req#)
          bad-meth# (nil? (some #(= req-meth# %) ~meths))
          any-meth# (= ~meths [:any])]
      (if (:uri req#)
        (if (and (route-matches ~uri req#) (and bad-meth# (not any-meth#)))
          (method-not-allowed req-meth# (get-allowed ~meths))
          (if-let [params# (route-matches ~uri req#)]
              (~handler (assoc req# :route-params params#))
              req#))
        req#))))


(defn wrap-routes [from-ns req]
  (or (first (filter :status
                     (for [[_ f] (ns-publics from-ns)
                           :when (:route-handler (meta f))]
                       (f req))))
      not-found))


(defn json-response
  [body]
  (->
    (response (generate-string body {:pretty true}))
    (content-type "application/json")))


(defn html-response
  [body]
  (->
    (response body)
    (content-type "text/html")))


(def render-not-found
  (str 
    "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 3.2 Final//EN\">
    <title>404 Not Found</title>
    <h1>Not Found</h1>
    <p>The requested URL was not found on the server.</p>
    <p>If you entered the URL manually please check your spelling and try "
    "again.</p>"))


(def not-found
  (html-response render-not-found))


(defn permanent-redirect
  [url]
  {:status 301
   :headers {"Location" url}
   :body ""})
