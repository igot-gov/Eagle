export class MetaTag {
    name: string
    value: string
    isFacebook: boolean

    constructor(name: string, value: string, isFacebook: boolean) {
        this.name = name
        this.value = value
        this.isFacebook = isFacebook
    }
}
