import { createSlice, createEntityAdapter } from '@reduxjs/toolkit';

import getLogger from '../lib/logger.js';

const log = getLogger('slice tasks');
log.enabled = true;

const adapter = createEntityAdapter();
const initialState = adapter.getInitialState();

export const tasksSlice = createSlice({
  name: 'tasks',
  initialState,
  reducers: {
    addTasks: adapter.addMany,
    addTask: adapter.addOne,
    updateTask(state, { payload }) {
      adapter.updateOne(state, { id: payload.id, changes: payload });
    },
    removeTask: adapter.removeOne,
  },
});

export const selectors = adapter.getSelectors((state) => state.tasks);
export const { actions } = tasksSlice;
export default tasksSlice.reducer;
