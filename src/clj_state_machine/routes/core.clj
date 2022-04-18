(ns clj-state-machine.routes.core
  (:require [io.pedestal.http.route :as route]
            [clj-state-machine.routes.status :as r.status]
            [clj-state-machine.routes.workflow :as r.workflow]
            [io.pedestal.http.body-params :as p.body-params]
            [clj-state-machine.interceptors :as interceptors]))

(def common-interceptors
  [(p.body-params/body-params)
   interceptors/json-out])

(def all-routes
  (route/expand-routes
    #{["/status/:status-id" :get (conj common-interceptors `r.status/get-status) :route-name :get-status]
      ["/status" :post (conj common-interceptors `r.status/post-status) :route-name :post-status]
      ["/status" :patch (conj common-interceptors `r.status/patch-status) :route-name :patch-status]
      ["/status/:status-id" :delete (conj common-interceptors `r.status/delete-status) :route-name :delete-status]
      ["/workflow/:workflow-id" :get (conj common-interceptors `r.workflow/get-workflow) :route-name :get-workflow]
      ["/workflow" :post (conj common-interceptors `r.workflow/post-workflow) :route-name :post-workflow]
      ["/workflow" :patch (conj common-interceptors `r.workflow/patch-workflow) :route-name :patch-workflow]
      ["/workflow/:workflow-id" :delete (conj common-interceptors `r.workflow/delete-workflow) :route-name :delete-workflow]}))
