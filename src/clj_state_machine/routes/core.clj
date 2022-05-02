(ns clj-state-machine.routes.core
  (:require [io.pedestal.http.route :as route]
            [clj-state-machine.routes.status :as r.status]
            [clj-state-machine.routes.workflow :as r.workflow]
            [clj-state-machine.routes.transition :as r.transition]
            [io.pedestal.http.body-params :as p.body-params]
            [pedestal-api-helper.interceptors :as api-h.i]))

(def common-interceptors
  [(p.body-params/body-params)
   api-h.i/json-out])

(def all-routes
  (route/expand-routes
    #{["/status/:status-id" :get (conj common-interceptors `r.status/get-status) :route-name :get-status]
      ["/status" :get (conj common-interceptors `r.status/get-status) :route-name :get-all-status]
      ["/status" :post (conj common-interceptors `r.status/post-status) :route-name :post-status]
      ["/status" :patch (conj common-interceptors `r.status/patch-status) :route-name :patch-status]
      ["/status/:status-id" :delete (conj common-interceptors `r.status/delete-status) :route-name :delete-status]
      ["/workflow/:workflow-id" :get (conj common-interceptors `r.workflow/get-workflow) :route-name :get-workflow]
      ["/workflow" :get (conj common-interceptors `r.workflow/get-workflow) :route-name :get-all-workflow]
      ["/workflow" :post (conj common-interceptors `r.workflow/post-workflow) :route-name :post-workflow]
      ["/workflow" :patch (conj common-interceptors `r.workflow/patch-workflow) :route-name :patch-workflow]
      ["/workflow/:workflow-id" :delete (conj common-interceptors `r.workflow/delete-workflow) :route-name :delete-workflow]
      ["/transition/:transition-id" :get (conj common-interceptors `r.transition/get-transition) :route-name :get-transition]
      ["/transition" :get (conj common-interceptors `r.transition/get-transition) :route-name :get-all-transition]
      ["/workflow/:workflow-id/transition" :post (conj common-interceptors `r.transition/post-transition) :route-name :post-transition]
      ["/workflow/:workflow-id/transition" :patch (conj common-interceptors `r.transition/patch-transition) :route-name :patch-transition]
      ["/transition/:transition-id" :delete (conj common-interceptors `r.workflow/delete-workflow) :route-name :delete-transition]}))
