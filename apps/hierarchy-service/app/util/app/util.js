function isValidEmail(email) {
  var re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/; // eslint-disable-line
  return re.test(String(email).toLowerCase());
}

function isNumeric(num) {
  return !Number.isNaN(num);
}

function getDateDifferenceInHours(date1, date2) {
  return Math.abs(date1.valueOf() - date2.valueOf()) / 1000 / 60 / 60;
}

function getDateDifference(date1, date2) {
  return Math.abs(date1.valueOf() - date2.valueOf());
}

function arrayUnique(value, index, self) {
  return self.indexOf(value) === index;
}

function isEmpty(obj) {
  return Object.keys(obj).length === 0 && obj.constructor === Object;
}

module.exports = {
  isValidEmail,
  isNumeric,
  getDateDifferenceInHours,
  getDateDifference,
  arrayUnique,
  isEmpty,
};
