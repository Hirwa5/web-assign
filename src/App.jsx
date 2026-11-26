import React, { useState } from 'react'
import Office from './components/office'
import OfficeList from './components/OfficeList'
import LibraryButton from './components/LibraryButton'

function App() {

  const [showList, setShowList] = useState(true);

  return (
    <div>
      <br />
      <LibraryButton buttonLabel="ALL OFFICES" buttonAction={() => setShowList(true)} />{" "}
      <LibraryButton buttonLabel="SAVE OFFICE" buttonAction={() => setShowList(false)} />

      {showList ? <OfficeList /> : <Office />}
    </div>
  )
}

export default App
