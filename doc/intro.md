# Introduction to clj-state-machine

- [Status](#status)
- [Transition](#transition)
- [Workflow](#workflow)

## Status
Core domain of the state-machine, each application connected to the `clj-state-machine` will be able to configure and switch statuses between its local entities.   
### Structure:
- id: uuid
- name: string

### Endpoints:
- [GET] `/status/:status-id`: Get `status` by `status-id` [uuid]   
Example Response [200]:
```json
{
  "id": "1d4b8213-723f-40a4-be75-9131844cff74",
  "name": "created"
}
```
- [GET] `/status`: Get all `status`   
Example Response [200]:
```json
[{
  "id": "1d4b8213-723f-40a4-be75-9131844cff74",
  "name": "created"
},{
  "id": "aa62f1a6-0c55-4a5a-9f96-c6d761988464",
  "name": "done"
}]
```
- [POST] `/status`: Insert a new `status`      
  - Request:
    - name: string mandatory
  - Response [200]:
    - id: uuid
  - Example Request:
```json
{"name": "created"}
```
  - Example Response:
```json
{"payload":
  {
  "id": "1d4b8213-723f-40a4-be75-9131844cff74"
  }
}
```
- [PATCH] `/status`: Patch a `status`
    - Request:
        - id: uuid-string mandatory
        - name: string mandatory
    - Response [200]:
        - id: uuid
    - Example Request:
```json
{
  "id": "1d4b8213-723f-40a4-be75-9131844cff74",
  "name": "updated"
}
```
- Example Response:
```json
{"payload":
  {
  "id": "1d4b8213-723f-40a4-be75-9131844cff74"
  }
}
```
- [DELETE] `/status/:status-id`: Delete `status` by `status-id` [uuid]   
Response [204]

## Transition
Connect statuses, when a `transition` between statuses is set they are allowed to transition between them.

### Structure
- id: uuid
- name: string
- status-from: uuid from [Status]
- status-to: uuid from [Status]

### Endpoints:
- [GET] `/transition/:transition-id`: Get `transition` by `transition-id` [uuid]   
  Example Response [200]:
```json
{
  "id": "3b938a31-e97b-4964-a62a-2671c1435d9b",
  "name": "create",
  "status-from": {
    "id": "1d4b8213-723f-40a4-be75-9131844cff74",
    "name": "created"
  }
}
```
- [GET] `/transition`: Get all transitions   
  Example Response:
```json
[{
  "id": "3b938a31-e97b-4964-a62a-2671c1435d9b",
  "name": "create",
  "status-to": {
    "id": "1d4b8213-723f-40a4-be75-9131844cff74",
    "name": "created"
  }
},{
  "id": "eb078bb4-1c50-4d84-95b5-f235249cd8fa",
  "name": "finish",
  "status-from": {
    "id": "1d4b8213-723f-40a4-be75-9131844cff74",
    "name": "created"
  },
  "status-to": {
    "id": "aa62f1a6-0c55-4a5a-9f96-c6d761988464",
    "name": "done"
  }
}]
```
- [DELETE] `/transition/:transition-id`: Delete `transition` by `transition-id` [uuid]   
  Response [204]

- [POST] `/workflow/:worfklow-id/transition`: Insert a new `transition` into a `workflow`
    - Request path:
      - workflow-id: uuid mandatory
    - Request body:
        - name: string mandatory
        - status-from: string uuid optional
        - status-to: string uuid mandatory
    - Response [200]:
        - id: uuid
    - Example Request:
```json
{
  "name": "create",
  "status-to":  "1d4b8213-723f-40a4-be75-9131844cff74"
}
```
- Example Response:
```json
{"payload":
  {
  "id": "1d4b8213-723f-40a4-be75-9131844cff74"
  }
}
```

- [GET] `/workflow/:worfklow-id/transition`: Get transitions by `workflow-id` [uuid]   
  Example Response [200]:
```json
[{
  "id": "3b938a31-e97b-4964-a62a-2671c1435d9b",
  "name": "create",
  "status-to": {
    "id": "1d4b8213-723f-40a4-be75-9131844cff74",
    "name": "created"
  }
},{
  "id": "eb078bb4-1c50-4d84-95b5-f235249cd8fa",
  "name": "finish",
  "status-from": {
    "id": "1d4b8213-723f-40a4-be75-9131844cff74",
    "name": "created"
  },
  "status-to": {
    "id": "aa62f1a6-0c55-4a5a-9f96-c6d761988464",
    "name": "done"
  }
}]
```

- [PATCH] `/workflow/:worfklow-id/transition`: Patches `transition` properties from a `workflow`
    - Request path:
        - workflow-id: uuid mandatory
    - Request body (:warning: `id` + at least one of them, is mandatory):
        - id: uuid string mandatory
        - name: string optional
        - status-from: string uuid optional
        - status-to: string uuid optional
    - Response [200]:
        - id: uuid
    - Example Request:
```json
{
  "id": "1d4b8213-723f-40a4-be75-9131844cff74",
  "name": "create-new-name",
  "status-to":  "1d4b8213-723f-40a4-be75-9131844cff74"
}
```
- Example Response:
```json
{"payload":
  {
  "id": "1d4b8213-723f-40a4-be75-9131844cff74"
  }
}
```
- [GET] `/workflow/:workflow-id/transition/status-from`: Get all initial transitions (no `status-from`) by `workflow-id` [uuid]   
  Example Response [200]:
```json
[{
  "id": "3b938a31-e97b-4964-a62a-2671c1435d9b",
  "name": "create",
  "status-to": {
    "id": "1d4b8213-723f-40a4-be75-9131844cff74",
    "name": "created"
  }
}]
```
- [GET] `/workflow/:workflow-id/transition/status-from/:status-from`: Get all transitions by `workflow-id` [uuid] and `status-from` [uuid]
  Example Response [200]:
```json
[{
  "id": "eb078bb4-1c50-4d84-95b5-f235249cd8fa",
  "name": "finish",
  "status-from": {
    "id": "1d4b8213-723f-40a4-be75-9131844cff74",
    "name": "created"
  },
  "status-to": {
    "id": "aa62f1a6-0c55-4a5a-9f96-c6d761988464",
    "name": "done"
  }
}]
```

## Workflow
Group a list of transitions. Every `transtion` must belong to a `workflow`.

### Structure:
- id: uuid
- name: string

### Endpoints:
- [GET] `/workflow/:workflow-id`: Get `workflow` by `workflow-id` [uuid]   
  Example Response:
```json
{
  "id": "b6bb143b-c06a-48af-9e7f-49dbda390cf5",
  "name": "orders-wf"
}
```
- [GET] `/workflow`: Get all workflows   
  Example Response:
```json
[{
  "id": "b6bb143b-c06a-48af-9e7f-49dbda390cf5",
  "name": "orders-wf"
},{
  "id": "7b5c4b2a-e3ac-41cf-b738-aab5c57117b2",
  "name": "payments-wf"
}]
```
- [POST] `/workflow`: Insert a new `workflow`
    - Request:
        - name: string mandatory
    - Response [200]:
        - id: uuid
    - Example Request:
```json
{"name": "orders-wf"}
```
- Example Response:
```json
{"payload":
  {
  "id": "b6bb143b-c06a-48af-9e7f-49dbda390cf5"
  }
}
```
- [PATCH] `/workflow`: Updates a `workflow`
    - Request:
        - id: uuid-string mandatory
        - name: string mandatory
    - Response [200]:
        - id: uuid
    - Example Request:
```json
{
  "id": "b6bb143b-c06a-48af-9e7f-49dbda390cf5",
  "name": "updated-orders-wf"
}
```
- Example Response:
```json
{"payload":
  {
  "id": "b6bb143b-c06a-48af-9e7f-49dbda390cf5"
  }
}
```
- [DELETE] `/workflow/:workflow-id`: Delete `workflow` by `workflow-id` [uuid]   
  Response [204]
