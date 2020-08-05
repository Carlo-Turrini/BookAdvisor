export class LoggedInfo {
  private _id: number;
  private _admin: boolean;

  constructor() {
    this.id = null;
    this.admin = false;
  }

  get id(): number {
    return this._id;
  }

  set id(value: number) {
    this._id = value;
  }

  get admin(): boolean {
    return this._admin;
  }

  set admin(value: boolean) {
    this._admin = value;
  }
}
