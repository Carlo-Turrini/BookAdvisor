import { Prize } from './prize';

describe('Prize', () => {
  it('should create an instance', () => {
    expect(new Prize(1, 2002, 'Prize')).toBeTruthy();
  });
});
