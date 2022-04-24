(ns clj-state-machine.routes.workflow-test
  (:require [clojure.test :refer :all]
            [integration_core :refer :all]
            [io.pedestal.test :as p.test]
            [clj-state-machine.db.workflow :as db.workflow]
            [clj-state-machine.db.config :as db.config]
            [clojure.set :as cset]
            [pedestal-api-helper.params-helper :as p-helper]
            [datomic-helper.entity :as dh.entity])
  (:use clojure.pprint)
  (:import (java.util UUID)))

(setup)

(def workflow-present-datomic
  {:workflow/id (p-helper/uuid)
   :workflow/name "on-progress"})

(def workflow-to-update-datomic
  {:workflow/id (p-helper/uuid)
   :workflow/name "stucked"})

(def workflow-finished-present-datomic
  {:workflow/id (p-helper/uuid)
   :workflow/name "finished"})

(def workflow-to-delete-datomic
  {:workflow/id (p-helper/uuid)
   :workflow/name "delete-me"})

(defn- workflow-present-view
  []
  {:id (get workflow-present-datomic :workflow/id)
   :name (get workflow-present-datomic :workflow/name)})

(defn- all-workflows-view
  []
  [{:id (-> workflow-present-datomic
            :workflow/id
            (p-helper/uuid-as-string))
    :name (get workflow-present-datomic :workflow/name)}
   {:id (-> workflow-finished-present-datomic
            :workflow/id
            (p-helper/uuid-as-string))
    :name (get workflow-finished-present-datomic :workflow/name)}])

(defn- workflow-to-update-view
  []
  {:id (get workflow-to-update-datomic :workflow/id)
   :name (get workflow-to-update-datomic :workflow/name)})

(defn- workflow-to-delete-view
  []
  {:id (get workflow-to-delete-datomic :workflow/id)
   :name (get workflow-to-delete-datomic :workflow/name)})

(defn- insert-test-data []
  (db.workflow/upsert! workflow-present-datomic)
  (db.workflow/upsert! workflow-finished-present-datomic))

(insert-test-data)

(deftest get-workflow
  (testing "Already present workflow is gotten"
    (let [expected-resp (assoc base-message :payload (workflow-present-view))
          workflow-present-get-url (str "/workflow/"
                                      (id-as-string
                                        workflow-present-datomic
                                        :workflow/id))
          actual-resp (p.test/response-for (:core @test-server) :get workflow-present-get-url)]
      (is (= (map-as-json expected-resp) (:body actual-resp)))
      (is (= 200 (:status actual-resp)))))

  (testing "non uuid gives not found"
    (let [expected-resp {:message "workflow was not found with given id"}
          workflow-present-get-url "/workflow/39384"
          actual-resp (p.test/response-for (:core @test-server) :get workflow-present-get-url)]
      (is (= (map-as-json expected-resp) (:body actual-resp)))
      (is (= 404 (:status actual-resp)))))

  (testing "not present uuid gives not found"
    (let [expected-resp {:message "workflow was not found with given id"}
          workflow-present-get-url (str "/workflow/" (p-helper/uuid))
          actual-resp (p.test/response-for (:core @test-server) :get workflow-present-get-url)]
      (is (= (map-as-json expected-resp) (:body actual-resp)))
      (is (= 404 (:status actual-resp)))))

  (testing "When no id is sent all workflows are returned"
    (let [actual-resp (p.test/response-for (:core @test-server) :get "/workflow")
          body-map (->> actual-resp
                        :body
                        (json-as-map)
                        :payload)]
      (is (cset/subset? (set (all-workflows-view)) (set body-map)))
      (is (= 200 (:status actual-resp))))))

(deftest post-workflow
  (testing "insert with mandatory params works"
    (let [new-workflow (dissoc (workflow-present-view) :id)
          new-name "dispatching"
          new-workflow (assoc new-workflow :name new-name)
          actual-resp (p.test/response-for (:core @test-server)
                                           :post "/workflow"
                                           :headers json-header
                                           :body (map-as-json new-workflow))
          body-as-map (json-as-map (:body actual-resp))
          id-gotten (get-in body-as-map [:payload :id] 0)
          id-gotten-as-uuid (UUID/fromString id-gotten)
          conn (db.config/connect!)
          workflow-in-db (dh.entity/find-by-id conn :workflow/id id-gotten-as-uuid)]
      (is (not (= 0 id-gotten)))
      (is (= 200 (:status actual-resp)))
      (is (= (:workflow/id workflow-in-db)
             id-gotten-as-uuid))))
  (testing "insert with missing mandatory params gives 400"
    (let [actual-resp (p.test/response-for (:core @test-server)
                                           :post "/workflow"
                                           :headers json-header
                                           :body "")
          expected-resp {:message "", :validation-messages [{:field "name"
                                                              :message "Field :name is not present"}]}]
      (is (= 400 (:status actual-resp)))
      (is (= (map-as-json expected-resp) (:body actual-resp))))))

(deftest patch-workflow
  (testing "patch with mandatory params works"
    (let [new-name "preparing"
          new-workflow (assoc (workflow-to-update-view) :name new-name)
          actual-resp (p.test/response-for (:core @test-server)
                                           :patch "/workflow"
                                           :headers json-header
                                           :body (map-as-json new-workflow))
          id (:id (workflow-to-update-view))
          conn (db.config/connect!)
          workflow-in-db (dh.entity/find-by-id conn :workflow/id id)]
      (is (= 204 (:status actual-resp)))
      (is (= (:workflow/name workflow-in-db) new-name))))
  (testing "insert with missing mandatory params gives 400"
    (let [actual-resp (p.test/response-for (:core @test-server)
                                           :patch "/workflow"
                                           :headers json-header
                                           :body "")
          expected-resp {:message "", :validation-messages [{:field "id" :message "Field :id is not present"}
                                                            {:field "name" :message "Field :name is not present"}]}]
      (is (= 400 (:status actual-resp)))
      (is (= (map-as-json expected-resp) (:body actual-resp))))))

(deftest delete-workflow
  (testing "Deleting works"
    (let [workflow-present-get-url (str "/workflow/"
                                      (id-as-string
                                        workflow-to-delete-datomic
                                        :workflow/id))
          actual-resp (p.test/response-for (:core @test-server) :delete workflow-present-get-url)
          conn (db.config/connect!)
          id (:id (workflow-to-delete-view))
          workflow-in-db (dh.entity/find-by-id conn :workflow/id id)]
      (is (= 204 (:status actual-resp)))
      (is (not workflow-in-db))))

  (testing "non uuid gives 204"
    (let [workflow-present-get-url "/workflow/39384"
          actual-resp (p.test/response-for (:core @test-server) :delete workflow-present-get-url)]
      (is (= 204 (:status actual-resp)))))

  (testing "not present uuid gives 204"
    (let [workflow-present-get-url (str "/workflow/" (p-helper/uuid))
          actual-resp (p.test/response-for (:core @test-server) :delete workflow-present-get-url)]
      (is (= 204 (:status actual-resp)))))
  )
