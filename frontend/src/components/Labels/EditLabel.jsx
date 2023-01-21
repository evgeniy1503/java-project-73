// @ts-check

import React, { useEffect, useState } from 'react';
import { useDispatch } from 'react-redux';
import { useTranslation } from 'react-i18next';
import { Form, Button } from 'react-bootstrap';
import { useFormik } from 'formik';
import * as yup from 'yup';
import { useParams, useHistory } from 'react-router-dom';
import axios from 'axios';

import { actions as labelsActions } from '../../slices/labelsSlice.js';
import handleError from '../../utils.js';
import routes from '../../routes.js';
import { useAuth, useNotify } from '../../hooks/index.js';

import getLogger from '../../lib/logger.js';

const log = getLogger('client');

const getValidationSchema = () => yup.object().shape({});

const EditLabel = () => {
  const { t } = useTranslation();
  const params = useParams();
  const history = useHistory();
  const auth = useAuth();
  const notify = useNotify();
  const [label, setLabel] = useState(null);
  const dispatch = useDispatch();

  useEffect(() => {
    const fetchData = async () => {
      try {
        const { data } = await axios.get(routes.apiLabel(params.labelId),
          { headers: auth.getAuthHeader() });
        setLabel(data);
      } catch (e) {
        handleError(e, notify, history);
      }
    };
    fetchData();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const f = useFormik({
    enableReinitialize: true,
    initialValues: {
      name: label ? label.name : '',
    },
    validationSchema: getValidationSchema(),
    onSubmit: async ({ name }, { setSubmitting, setErrors }) => {
      const newLabel = { name };
      try {
        log('label.edit', label);
        const { data } = await axios.put(routes.apiLabel(params.labelId),
          newLabel, { headers: auth.getAuthHeader() });
        dispatch(labelsActions.updateLabel(data));
        const from = { pathname: routes.labelsPagePath() };
        history.push(from, { message: 'labelEdited' });
      } catch (e) {
        log('label.edit.error', e);
        setSubmitting(false);
        if (e.response?.status === 422 && Array.isArray(e.response?.data)) {
          const errors = e.response.data
            .reduce((acc, err) => ({ ...acc, [err.field]: err.defaultMessage }), {});
          setErrors(errors);
          notify.addError('labelEditFail');
        } else {
          handleError(e, notify, history, auth);
        }
      }
    },
    validateOnBlur: false,
    validateOnChange: false,
  });

  if (!label) {
    return null;
  }

  return (
    <>
      <h1 className="my-4">{t('labelEdit')}</h1>
      <Form onSubmit={f.handleSubmit}>
        <Form.Group className="mb-3">
          <Form.Label htmlFor="name">{t('naming')}</Form.Label>
          <Form.Control
            className="mb-2"
            disabled={f.isSubmitting}
            onChange={f.handleChange}
            onBlur={f.handleBlur}
            value={f.values.name}
            isInvalid={f.errors.name && f.touched.name}
            name="name"
            id="name"
            type="text"
          />
          <Form.Control.Feedback type="invalid">
            {t(f.errors.name)}
          </Form.Control.Feedback>
        </Form.Group>
        <Button variant="primary" type="submit" disabled={f.isSubmitting}>
          {t('edit')}
        </Button>
      </Form>
    </>
  );
};

export default EditLabel;
