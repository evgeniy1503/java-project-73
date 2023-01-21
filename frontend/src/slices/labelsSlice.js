import { createSlice, createEntityAdapter } from '@reduxjs/toolkit';

import getLogger from '../lib/logger.js';

const log = getLogger('slice labels');
log.enabled = true;

const adapter = createEntityAdapter();
const initialState = adapter.getInitialState();

export const labelsSlice = createSlice({
  name: 'labels',
  initialState,
  reducers: {
    addLabels: adapter.addMany,
    addLabel: adapter.addOne,
    updateLabel(state, { payload }) {
      adapter.updateOne(state, { id: payload.id, changes: payload });
    },
    removeLabel: adapter.removeOne,
  },
});

export const selectors = adapter.getSelectors((state) => state.labels);
export const { actions } = labelsSlice;
export default labelsSlice.reducer;
