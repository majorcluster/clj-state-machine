(ns clj-state-machine.ports.http-in.routes.core
  (:require [clj-state-machine.ports.http-in.routes.interceptors :as i]
            [clj-state-machine.ports.http-in.routes.status :as r.status]
            [clj-state-machine.ports.http-in.routes.transition :as r.transition]
            [clj-state-machine.ports.http-in.routes.workflow :as r.workflow]
            [io.pedestal.http :as http]
            [io.pedestal.http.body-params :as body-params]))

(def common-interceptors [(body-params/body-params)
                          http/html-body])

(def json-interceptors [(body-params/body-params)
                        (i/json-out)
                        http/html-body])

(def specs #{["/status/:status-id" :get (conj json-interceptors `r.status/get-status) :route-name :get-status]
             ["/status" :get (conj json-interceptors `r.status/get-status) :route-name :get-all-status]
             ["/status" :post (conj json-interceptors `r.status/post-status) :route-name :post-status]
             ["/status" :patch (conj json-interceptors `r.status/patch-status) :route-name :patch-status]
             ["/status/:status-id" :delete (conj common-interceptors `r.status/delete-status) :route-name :delete-status]
             ["/workflow/:workflow-id" :get (conj json-interceptors `r.workflow/get-workflow) :route-name :get-workflow]
             ["/workflow" :get (conj json-interceptors `r.workflow/get-workflow) :route-name :get-all-workflow]
             ["/workflow" :post (conj json-interceptors `r.workflow/post-workflow) :route-name :post-workflow]
             ["/workflow" :patch (conj json-interceptors `r.workflow/patch-workflow) :route-name :patch-workflow]
             ["/workflow/:workflow-id" :delete (conj common-interceptors `r.workflow/delete-workflow) :route-name :delete-workflow]
             ["/transition/:transition-id" :get (conj json-interceptors `r.transition/get-transition) :route-name :get-transition]
             ["/transition" :get (conj json-interceptors `r.transition/get-transition) :route-name :get-all-transition]
             ["/workflow/:workflow-id/transition" :post (conj json-interceptors `r.transition/post-transition) :route-name :post-transition]
             ["/workflow/:workflow-id/transition" :patch (conj json-interceptors `r.transition/patch-transition) :route-name :patch-transition]
             ["/transition/:transition-id" :delete (conj common-interceptors `r.workflow/delete-workflow) :route-name :delete-transition]})
