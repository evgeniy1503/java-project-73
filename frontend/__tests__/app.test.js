// @ts-check

import 'core-js/stable';
import 'regenerator-runtime/runtime';
import '@testing-library/jest-dom';

import userEvent from '@testing-library/user-event';
import { render, screen, waitFor } from '@testing-library/react';
import { setupServer } from 'msw/node';
import { rest } from 'msw';

import getUsers from '../__fixtures__/executors.js';
import getLabels from '../__fixtures__/labels.js';
import getStatuses from '../__fixtures__/statuses.js';

import mocks from '../mocks/mocks.js';

import init from '../src/init.jsx';

const server = setupServer();
const user = {
  email: 'ivan@google.com',
  password: 'some-password',
};

const users = getUsers();
const labels = getLabels();
const taskStatuses = getStatuses();

afterAll(() => {
  server.close();
});

beforeEach(async () => {
  mocks.mockServer(server, rest);
  global.localStorage.clear();
  const vdom = await init();
  render(vdom);
});

afterEach(() => {
  server.resetHandlers();
});

describe('auth', () => {
  test('successful login', async () => {
    userEvent.click(await screen.findByRole('link', { name: /Вход/i }));
    expect(window.location.pathname).toBe('/login');
    expect(await screen.findByLabelText(/Email/i)).toBeInTheDocument();
    expect(await screen.findByLabelText(/Пароль/i)).toBeInTheDocument();
    userEvent.type(await screen.findByLabelText(/Email/i), user.email);
    userEvent.type(await screen.findByLabelText(/Пароль/i), user.password);
    userEvent.click(await screen.findByRole('button', { name: /Войти/i }));
    await waitFor(() => {
      expect(window.location.pathname).toBe('/');
    });
  });
});

describe('work', () => {
  beforeEach(async () => {
    userEvent.click(await screen.findByRole('link', { name: /Вход/i }));
    userEvent.type(await screen.findByLabelText(/Email/i), user.email);
    userEvent.type(await screen.findByLabelText(/Пароль/i), user.password);
    userEvent.click(await screen.findByRole('button', { name: /Войти/i }));
  });

  test('create task status', async () => {
    userEvent.click(await screen.findByText(/Статусы/i));
    userEvent.click(await screen.findByText(/создать статус/i));
    userEvent.type(await screen.findByLabelText(/Наименование/i), 'новый статус');
    userEvent.click(await screen.findByRole('button', { name: /Создать/i }));
    expect(await screen.findByText('новый статус')).toBeInTheDocument();
    expect(await screen.findByText('Статус успешно создан')).toBeInTheDocument();
  });

  test('edit task status', async () => {
    userEvent.click(await screen.findByText(/Статусы/i));
    userEvent.click(screen.getAllByText('Изменить')[0]);
    expect(await screen.findByText('Изменение статуса')).toBeInTheDocument();
    userEvent.clear(await screen.findByLabelText(/Наименование/i));
    userEvent.type(await screen.findByLabelText(/Наименование/i), 'измененный статус');
    userEvent.click(await screen.findByRole('button', { name: /изменить/i }));
    expect(await screen.findByText('измененный статус')).toBeInTheDocument();
    expect(await screen.findByText('Статус успешно изменён')).toBeInTheDocument();
  });

  test('delete task status', async () => {
    userEvent.click(await screen.findByText(/Статусы/i));
    userEvent.click(screen.getAllByText('Удалить')[0]);
    await waitFor(() => {
      expect(screen.queryByText(taskStatuses[0].name)).not.toBeInTheDocument();
    });
  });

  test('create label', async () => {
    userEvent.click(await screen.findByText(/Метки/i));
    userEvent.click(await screen.findByText(/создать метку/i));
    userEvent.type(await screen.findByLabelText(/Наименование/i), 'новая метка');
    userEvent.click(await screen.findByRole('button', { name: /Создать/i }));
    expect(await screen.findByText('новая метка')).toBeInTheDocument();
    expect(await screen.findByText('Метка успешно создана')).toBeInTheDocument();
  });

  test('edit label', async () => {
    userEvent.click(await screen.findByText(/Метки/i));
    userEvent.click(screen.getAllByText('Изменить')[0]);
    expect(await screen.findByText('Изменение метки')).toBeInTheDocument();
    userEvent.clear(await screen.findByLabelText(/Наименование/i));
    userEvent.type(await screen.findByLabelText(/Наименование/i), 'измененная метка');
    userEvent.click(await screen.findByRole('button', { name: /изменить/i }));
    expect(await screen.findByText('измененная метка')).toBeInTheDocument();
    expect(await screen.findByText('Метка успешно изменена')).toBeInTheDocument();
  });

  test('delete label', async () => {
    userEvent.click(await screen.findByText(/Метки/i));
    userEvent.click(screen.getAllByText('Удалить')[0]);
    await waitFor(() => {
      expect(screen.queryByText(labels[0].name)).not.toBeInTheDocument();
    });
  });

  test('create task', async () => {
    userEvent.click(await screen.findByText(/Задачи/i));
    expect(await screen.findByText(/создать задачу/i)).toBeInTheDocument();
    userEvent.click(await screen.findByText(/создать задачу/i));
    userEvent.type(await screen.findByLabelText(/Наименование/i), 'новая задача1');
    userEvent.type(await screen.findByLabelText(/Описание/i), 'описание задачи');
    userEvent.selectOptions(screen.getByLabelText('Статус'), [taskStatuses[0].name]);
    userEvent.selectOptions(screen.getByRole('listbox', { name: 'Метки' }), [labels[0].name, labels[1].name]);
    userEvent.selectOptions(screen.getByLabelText('Исполнитель'), `${users[0].firstName} ${users[0].lastName}`);
    userEvent.click(await screen.findByRole('button', { name: /Создать/i }));
    expect(await screen.findByText('новая задача1')).toBeInTheDocument();
    expect(await screen.findAllByText(taskStatuses[0].name)).toHaveLength(3);
    expect(await screen.findAllByText(`${users[0].firstName} ${users[0].lastName}`)).toHaveLength(4);
    expect(await screen.findByText('Задача успешно создана')).toBeInTheDocument();
  });

  test('edit task', async () => {
    userEvent.click(await screen.findByText(/Задачи/i));
    userEvent.click(screen.getAllByText('Изменить')[0]);

    userEvent.clear(await screen.findByLabelText(/Наименование/i));
    userEvent.type(await screen.findByLabelText(/Наименование/i), 'новая задача 1');
    userEvent.clear(await screen.findByLabelText(/Описание/i));
    userEvent.type(await screen.findByLabelText(/Описание/i), 'описание задачи 1');
    userEvent.selectOptions(screen.getByLabelText('Статус'), [taskStatuses[1].name]);
    userEvent.selectOptions(screen.getByRole('listbox', { name: 'Метки' }), [labels[2].name]);
    userEvent.selectOptions(screen.getByLabelText('Исполнитель'), `${users[1].firstName} ${users[1].lastName}`);
    userEvent.click(await screen.findByRole('button', { name: /Изменить/i }));

    expect(await screen.findByText('новая задача 1')).toBeInTheDocument();
    expect(await screen.findAllByText(taskStatuses[1].name)).toHaveLength(2);
    expect(await screen.findByText(`${users[1].firstName} ${users[1].lastName}`)).toBeInTheDocument();
    expect(await screen.findByText('Задача успешно отредактирована')).toBeInTheDocument();
  });

  test('delete task', async () => {
    userEvent.click(await screen.findByText(/Задачи/i));
    userEvent.click(await screen.findByText(/создать задачу/i));
    userEvent.type(await screen.findByLabelText(/Наименование/i), 'новая задача remove');
    userEvent.type(await screen.findByLabelText(/Описание/i), 'описание задачи remove');
    userEvent.selectOptions(screen.getByLabelText('Статус'), [taskStatuses[0].name]);
    userEvent.selectOptions(screen.getByRole('listbox', { name: 'Метки' }), [labels[0].name, labels[1].name]);
    userEvent.selectOptions(screen.getByLabelText('Исполнитель'), `${users[0].firstName} ${users[0].lastName}`);
    userEvent.click(await screen.findByRole('button', { name: /Создать/i }));
    expect(await screen.findByText('новая задача remove')).toBeInTheDocument();

    const removeButtons = screen.getAllByText('Удалить');
    userEvent.click(removeButtons[removeButtons.length - 1]);
    await waitFor(() => {
      expect(screen.queryByText('новая задача remove')).not.toBeInTheDocument();
    });
  });
});

describe('user', () => {
  test('create user', async () => {
    userEvent.click(await screen.findByText(/Регистрация/i));
    userEvent.type(await screen.findByLabelText(/Имя/i), 'FirstName');
    userEvent.type(await screen.findByLabelText(/Фамилия/i), 'LastName');
    userEvent.type(await screen.findByLabelText(/Email/i), 'test_email@google.com');
    userEvent.type(await screen.findByLabelText(/Пароль/i), 'password');
    userEvent.click(await screen.findByText(/Сохранить/i));
    expect(await screen.findByText('Успешная регистрация')).toBeInTheDocument();

    userEvent.click(await screen.findByText(/Пользователи/i));

    expect(await screen.findByText('FirstName LastName')).toBeInTheDocument();
    expect(await screen.findByText('test_email@google.com')).toBeInTheDocument();
  });

  test('edit user', async () => {
    userEvent.click(await screen.findByRole('link', { name: /Вход/i }));
    userEvent.type(await screen.findByLabelText(/Email/i), user.email);
    userEvent.type(await screen.findByLabelText(/Пароль/i), user.password);
    userEvent.click(await screen.findByRole('button', { name: /Войти/i }));
    expect(await screen.findByText('Вы авторизованы')).toBeInTheDocument();

    userEvent.click(await screen.findByRole('link', { name: /Пользователи/i }));

    expect(await screen.findByText('Полное имя')).toBeInTheDocument();

    userEvent.click(screen.getAllByText('Изменить')[0]);

    userEvent.clear(await screen.findByLabelText(/Имя/i));
    userEvent.type(await screen.findByLabelText(/Имя/i), 'FirstName edit');
    userEvent.clear(await screen.findByLabelText(/Фамилия/i));
    userEvent.type(await screen.findByLabelText(/Фамилия/i), 'LastName edit');
    userEvent.clear(await screen.findByLabelText(/Email/i));
    userEvent.type(await screen.findByLabelText(/Email/i), 'test_email_edit@google.com');
    userEvent.clear(await screen.findByLabelText(/Пароль/i));
    userEvent.type(await screen.findByLabelText(/Пароль/i), 'password');
    userEvent.click(await screen.findByText(/Изменить/i));

    expect(await screen.findByText('Пользователь успешно изменён')).toBeInTheDocument();
    expect(await screen.findByText('FirstName edit LastName edit')).toBeInTheDocument();
    expect(await screen.findByText('test_email_edit@google.com')).toBeInTheDocument();
  });

  test('delete user', async () => {
    userEvent.click(await screen.findByText(/Пользователи/i));
    const removeButtons = screen.getAllByText('Удалить');
    userEvent.click(removeButtons[0]);
    await waitFor(() => {
      expect(screen.queryByText(`${users[0].firstName} ${users[0].lastName}`)).not.toBeInTheDocument();
    });
  });
});
