const state = {
  theme: 'dark',
  test: true,
  gradient:[

    "#d05c7c",
    "#bb60b2",
    "rgb(255, 0, 135)",
    "rgb(135, 0, 157)",
    "rgb(255, 191, 0)",
    "rgb(224, 62, 76)",

    "rgb(128, 255, 165)",
    "rgb(1, 191, 236)",
    "rgb(0, 221, 255)",
    "rgb(77, 119, 255)",
    "rgb(55, 162, 255)",
    "rgb(116, 21, 219)",

  ],
  science:[
    "#05f8d6",
    "#0082fc",
    "#fdd845",
    "#22ed7c",
    "#09b0d3",
    "#1d27c9",
    "#f9e264",
    "#f47a75",
    "#009db2",
    "#024b51",
    "#0780cf",
    "#765005",
  ]
}
const mutations = {
  changeTheme (state) {
    if (state.theme === 'dark') {
      state.theme = 'default';
    } else {
      state.theme = 'dark';
    }
  }
}
const actions = {

}
export default {
  namespaced: true,
  state,
  mutations,
  actions
}
