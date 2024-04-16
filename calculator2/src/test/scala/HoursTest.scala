trait HoursTest extends munit.FunSuite:
  // This final test checks that you filled in the `time spent`
  // question at the end of the lab.
  test("After completing the lab, please report how long you spent on it (1pt)"):
    assert(howManyHoursISpentOnThisLab() > 0.0)
