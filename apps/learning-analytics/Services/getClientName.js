
exports.getClient = (req, res, next) => {
    req.client_name = req.get("rootorg").toLowerCase();
    console.log("Obtained lientc:" + req.client_name)
    if (!req.client_name) res.status(404).send({ message: "Client not Found" });
    else next();

}
