const getters = {
  sidebar: state => state.app.sidebar,
  device: state => state.app.device,
  token: state => state.auth.token,
  id: state => state.auth.id,
  account: state => state.auth.account,
  name: state => state.auth.name,
  email: state => state.auth.email,
  avatar: state => state.auth.avatar,
  role: state => state.auth.role
}
export default getters
