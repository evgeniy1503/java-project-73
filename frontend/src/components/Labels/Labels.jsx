// @ts-check

import React from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { useTranslation } from 'react-i18next';
import { Table, Form, Button } from 'react-bootstrap';
import axios from 'axios';
import { Link, useHistory } from 'react-router-dom';

import { actions as labelsActions, selectors } from '../../slices/labelsSlice.js';
import handleError from '../../utils.js';
import { useAuth, useNotify } from '../../hooks/index.js';
import routes from '../../routes.js';

import getLogger from '../../lib/logger.js';

const log = getLogger('labels');
log.enabled = true;

const Labels = () => {
  const { t } = useTranslation();
  const labels = useSelector(selectors.selectAll);
  const auth = useAuth();
  const notify = useNotify();
  const history = useHistory();
  const dispatch = useDispatch();

  const removeLabel = async (event, id) => {
    event.preventDefault();
    try {
      await axios.delete(routes.apiLabel(id), { headers: auth.getAuthHeader() });
      dispatch(labelsActions.removeLabel(id));
      notify.addMessage('labelRemoved');
    } catch (e) {
      if (e.response.status === 422) {
        notify.addError('labelRemoveFail');
      } else {
        handleError(e, notify, history, auth);
      }
    }
  };

  if (!labels) {
    return null;
  }

  return (
    <>
      <Link to={routes.newLabelPagePath()}>{t('createLabel')}</Link>
      <Table striped hover>
        <thead>
          <tr>
            <th>{t('id')}</th>
            <th>{t('statusName')}</th>
            <th>{t('createDate')}</th>
          </tr>
        </thead>
        <tbody>
          {labels.map((label) => (
            <tr key={label.id}>
              <td>{label.id}</td>
              <td>{label.name}</td>
              <td>{new Date(label.createdAt).toLocaleString('ru')}</td>
              <td>
                <Link to={routes.labelEditPagePath(label.id)}>{t('edit', { defaultValue: 'Изменить' })}</Link>
                <Form onSubmit={(event) => removeLabel(event, label.id)}>
                  <Button type="submit" variant="link">Удалить</Button>
                </Form>
              </td>
            </tr>
          ))}
        </tbody>
      </Table>
    </>
  );
};

export default Labels;
