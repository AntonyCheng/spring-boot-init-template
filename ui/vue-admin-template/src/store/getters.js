const getters = {
  sidebar: state => state.app.sidebar,
  device: state => state.app.device,
  token: state => state.auth.token,
  avatar: state => state.auth.avatar,
  name: state => state.auth.name
}
export default getters
