import { ExecutionOutputStore } from "./ExecutionOutput"
import { ExecutionStateStore } from './ExecutionState'
import { WorkflowStore } from './Workflow'
import { RundeckClient } from "@rundeck/client"

export class RootStore {
    executionOutputStore: ExecutionOutputStore
    workflowStore: WorkflowStore
    executionStateStore: ExecutionStateStore

    constructor(readonly client: RundeckClient) {
        this.executionOutputStore = new ExecutionOutputStore(this, client)
        this.workflowStore = new WorkflowStore(this, client)
        this.executionStateStore = new ExecutionStateStore(this, client)
    }
}