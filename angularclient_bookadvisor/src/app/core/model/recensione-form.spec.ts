import { RecensioneForm } from './recensione-form';

describe('RecensioneForm', () => {
  it('should create an instance', () => {
    expect(new RecensioneForm(4, 4, 4, 4, false, 'prova')).toBeTruthy();
  });
});
