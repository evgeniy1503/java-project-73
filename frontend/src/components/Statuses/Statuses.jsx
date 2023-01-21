// @ts-check

import React from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { useTranslation } from 'react-i18next';
import { Table, Form, Button } from 'react-bootstrap';
import axios from 'axios';
import { Link, useHistory } from 'react-router-dom';

import { actions, selectors } from '../../slices/taskStatusesSlice.js';
import handleError from '../../utils.js';
import { useAuth, useNotify } from '../../hooks/index.js';
import routes from '../../routes.js';

const Statuses = () => {
  const { t } = useTranslation();
  const auth = useAuth();
  const notify = useNotify();
  const history = useHistory();
  const dispatch = useDispatch();

  const taskStatuses = useSelector(selectors.selectAll);

  if (!taskStatuses) {
    return null;
  }

  const removeStatus = async (event, id) => {
    event.preventDefault();
    try {
      await axios.delete(routes.apiStatus(id), { headers: auth.getAuthHeader() });
      dispatch(actions.removeTaskStatus(id));
      notify.addMessage('statusRemoved');
    } catch (e) {
      if (e.response?.status === 422) {
        notify.addError('taskStatusRemoveFail');
      } else {
        handleError(e, notify, history, auth);
      }
    }
  };

  return (
    <>
      <Link to={routes.newStatusPagePath()}>{t('createStatus')}</Link>
      <Table striped hover>
        <thead>
          <tr>
            <th>{t('id')}</th>
            <th>{t('statusName')}</th>
            <th>{t('createDate')}</th>
          </tr>
        </thead>
        <tbody>
          {taskStatuses.map((status) => (
            <tr key={status.id}>
              <td>{status.id}</td>
              <td>{status.name}</td>
              <td>{new Date(status.createdAt).toLocaleString('ru')}</td>
              <td>
                <Link to={routes.statusEditPagePath(status.id)}>{t('edit')}</Link>
                <Form onSubmit={(event) => removeStatus(event, status.id)}>
                  <Button type="submit" variant="link">{t('remove')}</Button>
                </Form>
              </td>
            </tr>
          ))}
        </tbody>
      </Table>
    </>
  );
};

export default Statuses;
