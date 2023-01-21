/* eslint-disable no-param-reassign */
import { createSlice, createEntityAdapter } from '@reduxjs/toolkit';

import getLogger from '../lib/logger.js';

const log = getLogger('slice taskStatuses');
log.enabled = true;

const adapter = createEntityAdapter();
const initialState = adapter.getInitialState();

export const taskStatusesSlice = createSlice({
  name: 'taskStatuses',
  initialState,
  reducers: {
    addTaskStatuses: adapter.addMany,
    addTaskStatus: adapter.addOne,
    updateTaskStatus(state, { payload }) {
      adapter.updateOne(state, { id: payload.id, changes: payload });
    },
    removeTaskStatus: adapter.removeOne,
  },
});

export const selectors = adapter.getSelectors((state) => state.taskStatuses);
export const { actions } = taskStatusesSlice;
export default taskStatusesSlice.reducer;
