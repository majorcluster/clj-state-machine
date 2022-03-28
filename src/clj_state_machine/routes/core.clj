(ns clj-state-machine.routes.core
  (:require [io.pedestal.http.route :as route]
            [clj-state-machine.routes.status :as r.status]
            [io.pedestal.http.body-params :as p.body-params]
            [clj-state-machine.interceptors :as interceptors]))

(def common-interceptors
  [(p.body-params/body-params)
   interceptors/json-out])

(def all-routes
  (route/expand-routes
    #{["/status/:status-id" :get (conj common-interceptors `r.status/get-status) :route-name :get-status]
      ["/status" :post (conj common-interceptors `r.status/post-status) :route-name :post-status]}))
