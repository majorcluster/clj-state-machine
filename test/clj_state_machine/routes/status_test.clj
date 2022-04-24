(ns clj-state-machine.routes.status-test
  (:require [clojure.test :refer :all]
            [integration_core :refer :all]
            [io.pedestal.test :as p.test]
            [clj-state-machine.db.status :as db.status]
            [clj-state-machine.db.config :as db.config]
            [clojure.set :as cset]
            [matcher-combinators.test]
            [pedestal-api-helper.params-helper :as p-helper]
            [datomic-helper.entity :as dh.entity])
  (:use clojure.pprint)
  (:import (java.util UUID)))

(setup)

(def status-present-datomic
  {:status/id (p-helper/uuid)
   :status/name "on-progress"})

(def status-finished-present-datomic
  {:status/id (p-helper/uuid)
   :status/name "finished"})

(def status-to-update-datomic
  {:status/id (p-helper/uuid)
   :status/name "stucked"})

(def status-to-delete-datomic
  {:status/id (p-helper/uuid)
   :status/name "delete-me"})

(defn- status-present-view
  []
  {:id (get status-present-datomic :status/id)
   :name (get status-present-datomic :status/name)})

(defn- all-statuses-view
  []
  [{:id (-> status-present-datomic
            :status/id
            (p-helper/uuid-as-string))
   :name (get status-present-datomic :status/name)}
   {:id (-> status-finished-present-datomic
            :status/id
            (p-helper/uuid-as-string))
    :name (get status-finished-present-datomic :status/name)}])

(defn- status-to-update-view
  []
  {:id (get status-to-update-datomic :status/id)
   :name (get status-to-update-datomic :status/name)})

(defn- status-to-delete-view
  []
  {:id (get status-to-delete-datomic :status/id)
   :name (get status-to-delete-datomic :status/name)})

(defn- insert-test-data []
  (db.status/upsert! status-present-datomic)
  (db.status/upsert! status-finished-present-datomic))

(insert-test-data)

(deftest get-status
  (testing "Already present status is gotten"
    (let [expected-resp (assoc base-message :payload (status-present-view))
          status-present-get-url (str "/status/"
                                      (id-as-string
                                        status-present-datomic
                                        :status/id))
          actual-resp (p.test/response-for (:core @test-server) :get status-present-get-url)]
      (is (= (map-as-json expected-resp) (:body actual-resp)))
      (is (= 200 (:status actual-resp)))))

  (testing "When no id is sent all statuses are returned"
    (let [actual-resp (p.test/response-for (:core @test-server) :get "/status")
          body-map (->> actual-resp
                        :body
                        (json-as-map)
                        :payload)]
      (is (cset/subset? (set (all-statuses-view)) (set body-map)))
      (is (= 200 (:status actual-resp)))))

  (testing "non uuid gives not found"
    (let [expected-resp {:message "status was not found with given id"}
          status-present-get-url "/status/39384"
          actual-resp (p.test/response-for (:core @test-server) :get status-present-get-url)]
      (is (= (map-as-json expected-resp) (:body actual-resp)))
      (is (= 404 (:status actual-resp)))))

  (testing "not present uuid gives not found"
    (let [expected-resp {:message "status was not found with given id"}
          status-present-get-url (str "/status/" (p-helper/uuid))
          actual-resp (p.test/response-for (:core @test-server) :get status-present-get-url)]
      (is (= (map-as-json expected-resp) (:body actual-resp)))
      (is (= 404 (:status actual-resp))))))

(deftest post-status
  (testing "insert with mandatory params works"
    (let [new-status (dissoc (status-present-view) :id)
          new-name "dispatching"
          new-status (assoc new-status :name new-name)
          actual-resp (p.test/response-for (:core @test-server)
                                           :post "/status"
                                           :headers json-header
                                           :body (map-as-json new-status))
          body-as-map (json-as-map (:body actual-resp))
          id-gotten (get-in body-as-map [:payload :id] 0)
          id-gotten-as-uuid (UUID/fromString id-gotten)
          conn (db.config/connect!)
          status-in-db (dh.entity/find-by-id conn :status/id id-gotten-as-uuid)]
      (is (not (= 0 id-gotten)))
      (is (= 200 (:status actual-resp)))
      (is (= (:status/id status-in-db)
             id-gotten-as-uuid))))
  (testing "insert with missing mandatory params gives 400"
    (let [actual-resp (p.test/response-for (:core @test-server)
                                           :post "/status"
                                           :headers json-header
                                           :body "")
          expected-resp {:message "", :validation-messages [{:field "name" :message "Field :name is not present"}]}]
      (is (= 400 (:status actual-resp)))
      (is (= (map-as-json expected-resp) (:body actual-resp))))))

(deftest patch-status
  (testing "patch with mandatory params works"
    (let [new-name "preparing"
          new-status (assoc (status-to-update-view) :name new-name)
          actual-resp (p.test/response-for (:core @test-server)
                                           :patch "/status"
                                           :headers json-header
                                           :body (map-as-json new-status))
          id (:id (status-to-update-view))
          conn (db.config/connect!)
          status-in-db (dh.entity/find-by-id conn :status/id id)]
      (is (= 204 (:status actual-resp)))
      (is (= (:status/name status-in-db) new-name))))
  (testing "insert with missing mandatory params gives 400"
    (let [actual-resp (p.test/response-for (:core @test-server)
                                           :patch "/status"
                                           :headers json-header
                                           :body "")
          expected-resp {:message "", :validation-messages [{:field "id" :message "Field :id is not present"}
                                                            {:field "name" :message "Field :name is not present"}]}]
      (is (= 400 (:status actual-resp)))
      (is (= (map-as-json expected-resp) (:body actual-resp))))))

(deftest delete-status
  (testing "Deleting works"
    (let [status-present-get-url (str "/status/"
                                      (id-as-string
                                        status-to-delete-datomic
                                        :status/id))
          actual-resp (p.test/response-for (:core @test-server) :delete status-present-get-url)
          conn (db.config/connect!)
          id (:id (status-to-delete-view))
          status-in-db (dh.entity/find-by-id conn :status/id id)]
      (is (= 204 (:status actual-resp)))
      (is (not status-in-db))))

  (testing "non uuid gives 204"
    (let [status-present-get-url "/status/39384"
          actual-resp (p.test/response-for (:core @test-server) :delete status-present-get-url)]
      (is (= 204 (:status actual-resp)))))

  (testing "not present uuid gives 204"
    (let [status-present-get-url (str "/status/" (p-helper/uuid))
          actual-resp (p.test/response-for (:core @test-server) :delete status-present-get-url)]
      (is (= 204 (:status actual-resp)))))
  )